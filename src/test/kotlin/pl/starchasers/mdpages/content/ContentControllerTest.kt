package pl.starchasers.mdpages.content

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import no.skatteetaten.aurora.mockmvc.extensions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.result.JsonPathResultMatchers
import org.springframework.transaction.annotation.Transactional
import pl.starchasers.mdpages.*
import pl.starchasers.mdpages.authentication.TokenService
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.data.Page
import pl.starchasers.mdpages.content.data.dto.CreateFolderDTO
import pl.starchasers.mdpages.content.data.dto.CreatePageDTO
import pl.starchasers.mdpages.content.data.dto.FolderIdResponseDTO
import pl.starchasers.mdpages.content.data.dto.UpdatePageDTO
import pl.starchasers.mdpages.security.permission.PermissionService
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.user.UserService
import java.time.LocalDateTime

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
internal class ContentControllerTest(
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
    inner class CreateFolder : MockMvcTestBase() {

        private val createFolderRequestPath = Path("/api/content/folder")
        private lateinit var parent: Folder

        @BeforeEach
        fun createTestScope() {
            val folder = Folder(true, mutableSetOf(), "root", null, null)
            contentService.createFolder(folder)
            parent = folder
        }

        @DocumentResponse
        @Test
        fun `Given valid data, should create folder and return id`() {
            grantWritePermission(parent)
            mockMvc.put(
                path = createFolderRequestPath,
                body = mapper.writeValueAsString(CreateFolderDTO("testFolder", parent.id)),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isSuccess()
                responseJsonPath("$.folderId").equalsLong(contentService.getFolder(parent.id).children.toList()[0].id)
            }
        }

        @Test
        fun `Given duplicate folder name, should return 400`() {
            grantWritePermission(parent)
            contentService.createFolder(Folder(false, mutableSetOf(), "testFolder", parent, parent))
            mockMvc.put(
                path = createFolderRequestPath,
                body = mapper.writeValueAsString(CreateFolderDTO("testFolder", parent.id)),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
        }

        @Test
        fun `Given invalid parentId, should return 403`() {
            grantWritePermission(parent)
            mockMvc.put(
                path = createFolderRequestPath,
                body = mapper.writeValueAsString(CreateFolderDTO("testFolder", 999)),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
        }

        @Test
        fun `Given page as parentId, should return 400`() {
            //TODO: implement page creation
        }

        @Test
        fun `Given missing WRITE permission, should return 403`() {
            mockMvc.put(
                path = createFolderRequestPath,
                body = mapper.writeValueAsString(CreateFolderDTO("testFolder", parent.id)),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
        }
    }

    @Transactional
    @OrderTests
    @Nested
    inner class DeleteFolder() : MockMvcTestBase() {

        private val deleteFolderRequestPath = "/api/content/folder/"
        private lateinit var testFolder: Folder
        private lateinit var root: Folder

        @BeforeEach
        fun createTestFolder() {
            root = Folder(true, mutableSetOf(), "root", null, null)
            contentService.createFolder(root)
            testFolder = Folder(false, mutableSetOf(), "testFolder", root, root)
            contentService.createFolder(testFolder)
            flush()
        }

        fun deleteTestFolder() {
            contentService.deleteObjectRecursive(root)
            flush()
        }

        @DocumentResponse
        @Test
        fun `Given valid data, should delete folder`() {
            grantWritePermission(root)
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + testFolder.id),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isSuccess()
            }
            flush()
            assertEquals(0, contentService.getFolder(root.id).children.size)
        }

        @Test
        fun `Given not empty folder, should return 400`() {
            grantWritePermission(root)
            contentService.createFolder(Folder(false, mutableSetOf(), "child", testFolder, root))
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + testFolder.id),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
            flush()
            assertEquals(1, contentService.getFolder(root.id).children.size)
        }

        @Test
        fun `Given invalid folderId, should return 403`() {
            grantWritePermission(root)
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + 999),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
            flush()
            assertEquals(1, contentService.getFolder(root.id).children.size)
        }

        @Test
        fun `Given missing WRITE permission, should return 403`() {
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + testFolder.id),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
            flush()
            assertEquals(1, contentService.getFolder(root.id).children.size)
        }

        @Test
        fun `Given scope root, should return 403`() {
            grantWritePermission(root)
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + root.id),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
            flush()
            assertEquals(1, contentService.getFolder(root.id).children.size)
        }

        @Test
        fun `Given not authenticated user, should return 403`() {
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + testFolder.id),
                headers = HttpHeaders().contentTypeJson()
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
            flush()
            assertEquals(1, contentService.getFolder(root.id).children.size)
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
                assertEquals("testTitle", name)
                assertEquals("testContent", content)
                assertEquals("/root/testTitle", fullPath)
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
        fun `Given invalid parent folder id, should return 403`() {
            mockMvc.put(
                path = createPageRequest,
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(CreatePageDTO(rootFolderId + 1, "testTitle", "testContent"))
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
            flush()
            assertEquals(0, contentService.getFolder(rootFolderId).children.size)
        }

        @Test
        fun `Given missing WRITE permission, should return 403`() {
            mockMvc.put(
                path = createPageRequest,
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(CreatePageDTO(rootFolderId, "testTitle", "testContent"))
            ) {
                isError(HttpStatus.FORBIDDEN)
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
            assertEquals("testTitle", name)
            assertEquals("testContent", content)
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
                assertEquals("testTitle2", name)
                assertEquals("testContent2", content)
                assertEquals("/root/testTitle2", fullPath)
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
        fun `Given missing WRITE permission, should return 403`() {
            mockMvc.patch(
                path = getModifyPageRequest(testPageId),
                headers = HttpHeaders().contentTypeJson(),
                body = mapper.writeValueAsString(UpdatePageDTO("testTitle2", "testContent2"))
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
            flush()
            verifyPageUnchanged()
        }

        @Test
        fun `Given invalid page id, should return 403`() {
            mockMvc.patch(
                path = getModifyPageRequest(testPageId + 1),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken()),
                body = mapper.writeValueAsString(UpdatePageDTO("testTitle2", "testContent2"))
            ) {
                isError(HttpStatus.FORBIDDEN)
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
            assertNull(contentService.findPage(testPageId))
        }

        @Test
        fun `Given missing WRITE permission, should return 403`() {
            mockMvc.delete(
                path = getDeletePageRequest(testPageId),
                headers = HttpHeaders()
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
            flush()
            assertNotNull(contentService.findPage(testPageId))
        }

        @Test
        fun `Given invalid page id, should return 403`() {
            mockMvc.delete(
                path = getDeletePageRequest(testPageId + 1),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isError(HttpStatus.FORBIDDEN)
            }
            flush()
            assertNotNull(contentService.findPage(testPageId))
        }
    }
}