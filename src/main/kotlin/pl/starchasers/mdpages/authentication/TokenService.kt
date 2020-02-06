package pl.starchasers.mdpages.authentication

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pl.starchasers.mdpages.Util
import pl.starchasers.mdpages.user.UserNotFoundException
import pl.starchasers.mdpages.user.UserService
import pl.starchasers.mdpages.user.data.User
import java.time.LocalDateTime
import java.util.*


@Service
class TokenService(val refreshTokenRepository: RefreshTokenRepository, val userService: UserService) {

    @Value("\${jwt.secret}")
    private val secret = ""

    private val refreshTokenValidTime: Long = 7 * 24 * 60 * 60 * 1000
    private val accessTokenValidTime: Long = 15 * 60 * 1000


    fun issueRefreshToken(user: User): String {
        val claims = Jwts.claims().setSubject(user.id.toString())
        val now = Date()
        val tokenId = Util.randomString(64)

        claims["tokenId"] = tokenId
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
        val user = userService.getUser(oldClaims.subject?.toLong() ?: throw UserNotFoundException())

        verifyRefreshToken(oldClaims["tokenId"] as String, user)

        return issueRefreshToken(user)
    }


    fun issueAccessToken(refreshToken: String): String {
        val refreshTokenClaims = parseToken(refreshToken)
        val user = userService.getUser(refreshTokenClaims.subject?.toLong() ?: throw UserNotFoundException())

        val claims = Jwts.claims().setSubject(user.id.toString())
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

    fun parseToken(token: String): Claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body

    fun invalidateUser(user: User) {
        refreshTokenRepository.deleteAllByUser(user)
    }
}