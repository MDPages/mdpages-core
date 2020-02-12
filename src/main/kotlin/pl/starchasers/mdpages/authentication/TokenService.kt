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

//TODO exception handling
@Service
class TokenService(private val refreshTokenRepository: RefreshTokenRepository, private val userService: UserService) {

    @Value("\${jwt.secret}")
    private val secret = ""

    private val refreshTokenValidTime: Long = 7 * 24 * 60 * 60 * 1000
    private val accessTokenValidTime: Long = 15 * 60 * 1000

    fun issueRefreshToken(user: User): String {
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

    fun refreshRefreshToken(oldRefreshToken: String): String {
        val oldClaims = parseToken(oldRefreshToken)
        val user = userService.getUser(oldClaims.subject.toLong())

        verifyRefreshToken(oldClaims[TOKEN_ID_KEY] as String, user)

        return issueRefreshToken(user)
    }


    fun issueAccessToken(refreshToken: String): String {
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

    fun verifyRefreshToken(token: String, user: User) {
        refreshTokenRepository.findFirstByTokenAndUser(token, user) ?: throw InvalidTokenException()
    }

    fun parseToken(token: String): Claims {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
        } catch (e: Exception) {
            throw InvalidTokenException()
        }
    }

    fun invalidateUser(user: User) {
        refreshTokenRepository.deleteAllByUser(user)
    }

    @Transactional
    fun invalidateRefreshToken(refreshToken: String) {
        val claims = parseToken(refreshToken)
        refreshTokenRepository.deleteAllByToken(claims[TOKEN_ID_KEY] as String)
    }
}