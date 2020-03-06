package pl.starchasers.mdpages.content

import no.skatteetaten.aurora.mockmvc.extensions.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.starchasers.mdpages.*
import pl.starchasers.mdpages.authentication.TokenService
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.data.Page
import pl.starchasers.mdpages.content.data.dto.CreatePageDTO
import pl.starchasers.mdpages.content.data.dto.UpdatePageDTO
import pl.starchasers.mdpages.security.permission.PermissionService
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.user.UserService
import java.time.LocalDateTime

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
internal class PageControllerTest(
    @Autowired private val userService: UserService,
    @Autowired private val tokenService: TokenService,
    @Autowired private val permissionService: PermissionService,
    @Autowired private val contentService: ContentService
) : MockMvcTestBase() {
    @BeforeEach
    fun createTestUser() {
        userService.createUser("testUser", "password")
    }

    fun grantWritePermission(scope: Folder) {
        permissionService.grantScopePermission(scope, PermissionType.WRITE, userService.getUserByUsername("testUser"))
        flush()
    }

    fun getAccessToken(): String {
        return tokenService.issueAccessToken(tokenService.issueRefreshToken(userService.getUserByUsername("testUser")))
    }

    @Transactional
    @OrderTests
    @Nested
    inner class GetPage() : MockMvcTestBase() {
        private fun getRequestPath(id: Long): Path = Path("/api/content/page/$id")
        private var rootFolderId: Long = -1
        private var testPageId: Long = -1

        @BeforeEach
        fun setup() {
            val folder = Folder(true, mutableSetOf(), "root", null, null)
            contentService.createFolder(folder)
            rootFolderId = folder.id

            val page = contentService.createPage(rootFolderId, "testPage", "testContent")
            testPageId = page.id
            flush()
        }

        private fun grantReadPermission() {
            permissionService.grantScopePermission(
                contentService.getFolder(rootFolderId),
                PermissionType.READ,
                userService.getUserByUsername("testUser")
            )
            flush()
        }

        @DocumentResponse
        @Test
        fun `Given valid data, should return page details`() {
            grantReadPermission()

            mockMvc.get(
                path = getRequestPath(testPageId),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isSuccess()
                responseJsonPath("$.page.id").equalsLong(testPageId)
                responseJsonPath("$.page.name").equalsValue("testPage")
                responseJsonPath("$.page.content").equalsValue("testContent")
                responseJsonPath("$.page.dateCreated").isNotEmpty()
                responseJsonPath("$.page.dateModified").isNotEmpty()
                responseJsonPath("$.page.parentFolderId").equalsLong(rootFolderId)
                responseJsonPath("$.page.scopeFolderId").equalsLong(rootFolderId)
            }
        }

        @Test
        fun `Given invalid page id, should return 404`() {
            grantReadPermission()
            mockMvc.get(
                path = getRequestPath(testPageId + 1),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isError(HttpStatus.NOT_FOUND)
            }
        }

        @Test
        fun `Given missing READ permission, should return 401`() {
            mockMvc.get(
                path = getRequestPath(testPageId),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
        }
    }

    @Transactional
    @OrderTests
    @Nested
    inner class CreatePage() : MockMvcTestBase() {

        private val createPageRequest = Path("/api/content/page")
        private var rootFolderId: Long = -1

        @BeforeEach
        fun createTestFolder() {
            val folder = Folder(true, mutableSetOf(), "root", null, null)
            contentService.createFolder(folder)
            rootFolderId = folder.id

            grantWritePermission(folder)
            flush()
        }

        @DocumentResponse
        @Test
        fun `Given valid data, should create page and return 200`() {
            mockMvc.put(
                path = createPageRequest,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(CreatePageDTO(rootFolderId, "testTitle", "testContent"))
            ) {
                isSuccess()
                responseJsonPath("$.pageId").equalsLong(contentService.findFolder(rootFolderId)!!.children.elementAt(0).id)
            }
            flush()

            (contentService.getFolder(rootFolderId).children.elementAt(0) as Page).apply {
                Assertions.assertEquals("testTitle", name)
                Assertions.assertEquals("testContent", content)
                Assertions.assertEquals("/root/testTitle", fullPath)
            }
        }

        @Test
        fun `Given duplicate page title, should return 400`() {
            contentService.createPage(
                Page(
                    "testContent2",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    false,
                    "testTitle",
                    contentService.getFolder(rootFolderId),
                    contentService.getFolder(rootFolderId)
                )
            )
            flush()
            mockMvc.put(
                path = createPageRequest,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(CreatePageDTO(rootFolderId, "testTitle", "testContent"))
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
        }

        @Test
        fun `Given invalid parent folder id, should return 404`() {
            mockMvc.put(
                path = createPageRequest,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(CreatePageDTO(rootFolderId + 1, "testTitle", "testContent"))
            ) {
                isError(HttpStatus.NOT_FOUND)
            }
            flush()
            Assertions.assertEquals(0, contentService.getFolder(rootFolderId).children.size)
        }

        @Test
        fun `Given missing WRITE permission, should return 401`() {
            mockMvc.put(
                path = createPageRequest,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(CreatePageDTO(rootFolderId, "testTitle", "testContent"))
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
        }

        @Test
        fun `Given too long title, should return 400`() {
            mockMvc.put(
                path = createPageRequest,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(CreatePageDTO(rootFolderId, "a".repeat(65), "testContent"))
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }

        }

    }


    @Transactional
    @OrderTests
    @Nested
    inner class ModifyPage() : MockMvcTestBase() {

        private var rootFolderId: Long = -1
        private var testPageId: Long = -1

        private fun getModifyPageRequest(id: Long): Path = Path("/api/content/page/$id")

        @BeforeEach
        fun setup() {
            val rootFolder = Folder(true, mutableSetOf(), "root", null, null)
            contentService.createFolder(rootFolder)
            val page = contentService.createPage(rootFolder.id, "testTitle", "testContent")

            rootFolderId = rootFolder.id
            testPageId = page.id
            grantWritePermission(rootFolder)
        }

        private fun verifyPageUnchanged() = contentService.getPage(testPageId).apply {
            Assertions.assertEquals("testTitle", name)
            Assertions.assertEquals("testContent", content)
        }

        @DocumentResponse
        @Test
        fun `Given valid data, should update page and return 200`() {
            mockMvc.patch(
                path = getModifyPageRequest(testPageId),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(UpdatePageDTO("testTitle2", "testContent2"))
            ) {
                isSuccess()
            }
            flush()

            contentService.getPage(testPageId).apply {
                Assertions.assertEquals("testTitle2", name)
                Assertions.assertEquals("testContent2", content)
                Assertions.assertEquals("/root/testTitle2", fullPath)
            }

        }

        @Test
        fun `Given duplicate page title, should return 400`() {
            contentService.createPage(rootFolderId, "testTitle2", "testContent3")
            flush()
            mockMvc.patch(
                path = getModifyPageRequest(testPageId),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(UpdatePageDTO("testTitle2", "testContent2"))
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
            flush()
            verifyPageUnchanged()
        }

        @Test
        fun `Given empty title, should return 400`() {
            mockMvc.patch(
                path = getModifyPageRequest(testPageId),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(UpdatePageDTO("", "testContent2"))
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
            flush()
            verifyPageUnchanged()
        }

        @Test
        fun `Given too long title, should return 400`() {
            mockMvc.patch(
                path = getModifyPageRequest(testPageId),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(UpdatePageDTO("a".repeat(65), "testContent2"))
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
            flush()
            verifyPageUnchanged()
        }

        @Test
        fun `Given missing WRITE permission, should return 401`() {
            mockMvc.patch(
                path = getModifyPageRequest(testPageId),
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(UpdatePageDTO("testTitle2", "testContent2"))
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
            flush()
            verifyPageUnchanged()
        }

        @Test
        fun `Given invalid page id, should return 404`() {
            mockMvc.patch(
                path = getModifyPageRequest(testPageId + 1),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(UpdatePageDTO("testTitle2", "testContent2"))
            ) {
                isError(HttpStatus.NOT_FOUND)
            }
            flush()
            verifyPageUnchanged()
        }
    }

    @Transactional
    @OrderTests
    @Nested
    inner class DeletePage() : MockMvcTestBase() {

        private var rootFolderId: Long = -1
        private var testPageId: Long = -1

        @BeforeEach()
        fun setup() {
            val folder = Folder(true, mutableSetOf(), "root", null, null)
            contentService.createFolder(folder)
            val page = contentService.createPage(folder.id, "testTitle", "testContent")

            grantWritePermission(folder)
            rootFolderId = folder.id
            testPageId = page.id
        }

        private fun getDeletePageRequest(id: Long) = Path("/api/content/page/$id")

        @DocumentResponse
        @Test
        fun `Given valid data, should delete page and return 200`() {
            mockMvc.delete(
                path = getDeletePageRequest(testPageId),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isSuccess()
            }
            flush()
            Assertions.assertNull(contentService.findPage(testPageId))
        }

        @Test
        fun `Given missing WRITE permission, should return 401`() {
            mockMvc.delete(
                path = getDeletePageRequest(testPageId),
                headers = HttpHeaders()
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
            flush()
            Assertions.assertNotNull(contentService.findPage(testPageId))
        }

        @Test
        fun `Given invalid page id, should return 404`() {
            mockMvc.delete(
                path = getDeletePageRequest(testPageId + 1),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isError(HttpStatus.NOT_FOUND)
            }
            flush()
            Assertions.assertNotNull(contentService.findPage(testPageId))
        }
    }

}