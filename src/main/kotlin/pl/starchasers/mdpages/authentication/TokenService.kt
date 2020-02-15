package pl.starchasers.mdpages.authentication

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.starchasers.mdpages.user.exception.UserNotFoundException
import pl.starchasers.mdpages.user.UserService
import pl.starchasers.mdpages.user.data.User
import java.time.LocalDateTime
import java.util.*

private const val TOKEN_ID_KEY = "tokenId"

interface TokenService {
    fun issueRefreshToken(user: User): String

    fun refreshRefreshToken(oldRefreshToken: String): String

    fun issueAccessToken(refreshToken: String): String

    fun verifyRefreshToken(token: String, user: User)

    fun parseToken(token: String): Claims

    fun invalidateUser(user: User)

    fun invalidateRefreshToken(refreshToken: String)
}

@Service
class TokenServiceImpl(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userService: UserService
) : TokenService {

    @Value("\${jwt.secret}")
    private val secret = ""

    private val refreshTokenValidTime: Long = 7 * 24 * 60 * 60 * 1000
    private val accessTokenValidTime: Long = 15 * 60 * 1000

    override fun issueRefreshToken(user: User): String {
        val claims = Jwts.claims().setSubject(user.id.toString())
        val now = Date()
        val tokenId = UUID.randomUUID().toString()

        claims[TOKEN_ID_KEY] = tokenId
        val refreshToken = RefreshToken(
            0,
            user,
            tokenId,
            LocalDateTime.now(),
            LocalDateTime.now().plusNanos(refreshTokenValidTime * 1000)
        )

        refreshTokenRepository.save(refreshToken)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + refreshTokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }

    override fun refreshRefreshToken(oldRefreshToken: String): String {
        val oldClaims = parseToken(oldRefreshToken)
        val user = userService.getUser(oldClaims.subject.toLong())

        verifyRefreshToken(oldClaims[TOKEN_ID_KEY] as String, user)

        return issueRefreshToken(user)
    }


    override fun issueAccessToken(refreshToken: String): String {
        val refreshTokenClaims = parseToken(refreshToken)
        val user = userService.getUser(refreshTokenClaims.subject.toLong())

        val claims = Jwts.claims().setSubject(user.id.toString())

        verifyRefreshToken(refreshTokenClaims[TOKEN_ID_KEY] as String, user)

        val now = Date()

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + accessTokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()

    }

    override fun verifyRefreshToken(token: String, user: User) {
        refreshTokenRepository.findFirstByTokenAndUser(token, user) ?: throw InvalidTokenException()
    }

    override fun parseToken(token: String): Claims {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
        } catch (e: Exception) {
            throw InvalidTokenException()
        }
    }

    @Transactional
    override fun invalidateUser(user: User) {
        refreshTokenRepository.deleteAllByUser(user)
    }

    @Transactional
    override fun invalidateRefreshToken(refreshToken: String) {
        val claims = parseToken(refreshToken)
        refreshTokenRepository.deleteAllByToken(claims[TOKEN_ID_KEY] as String)
    }
}