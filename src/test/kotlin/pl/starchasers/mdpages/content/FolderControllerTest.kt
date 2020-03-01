package pl.starchasers.mdpages.content

import no.skatteetaten.aurora.mockmvc.extensions.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import pl.starchasers.mdpages.*
import pl.starchasers.mdpages.authentication.TokenService
import pl.starchasers.mdpages.content.data.Folder
import pl.starchasers.mdpages.content.data.dto.CreateFolderDTO
import pl.starchasers.mdpages.security.permission.PermissionService
import pl.starchasers.mdpages.security.permission.PermissionType
import pl.starchasers.mdpages.user.UserService

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
internal class FolderControllerTest(
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
            Assertions.assertEquals(0, contentService.getFolder(root.id).children.size)
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
            Assertions.assertEquals(1, contentService.getFolder(root.id).children.size)
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
            Assertions.assertEquals(1, contentService.getFolder(root.id).children.size)
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
            Assertions.assertEquals(1, contentService.getFolder(root.id).children.size)
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
            Assertions.assertEquals(1, contentService.getFolder(root.id).children.size)
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
            Assertions.assertEquals(1, contentService.getFolder(root.id).children.size)
        }
    }


}