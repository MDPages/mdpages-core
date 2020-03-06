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

    fun grantReadPermission(scope: Folder) {
        permissionService.grantScopePermission(scope, PermissionType.READ, userService.getUserByUsername("testUser"))
        flush()
    }

    fun getAccessToken(): String {
        return tokenService.issueAccessToken(tokenService.issueRefreshToken(userService.getUserByUsername("testUser")))
    }

    @Transactional
    @OrderTests
    @Nested
    inner class GetScopes : MockMvcTestBase() {

        private val getScopesRequest = Path("/api/content/folder/scopes")

        @DocumentResponse
        @Test
        fun `Given valid request, should return readable scopes`() {
            val folder = Folder(true, mutableSetOf(), "testScope", null, null)
            contentService.createFolder(folder)
            grantReadPermission(folder)
            flush()

            mockMvc.get(
                path = getScopesRequest,
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isSuccess()
                responseJsonPath("$.scopes[0].name").equalsValue("testScope")
                responseJsonPath("$.scopes[0].id").equalsLong(folder.id)
            }
        }

        @Test
        fun `Given missing READ permission, should not return scope`() {
            val folder = Folder(true, mutableSetOf(), "testScope", null, null)
            contentService.createFolder(folder)
            flush()

            mockMvc.get(
                path = getScopesRequest,
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isSuccess()
                responseJsonPath("$.scopes").isEmpty()
            }
        }
    }

    @Transactional
    @OrderTests
    @Nested
    inner class GetFolder() : MockMvcTestBase() {
        private fun getFolderRequest(id: Long) = Path("/api/content/folder/$id")
        private var rootFolderId: Long = -1

        @BeforeEach
        fun setup() {
            val root = Folder(true, mutableSetOf(), "root", null, null)
            contentService.createFolder(root)
            rootFolderId = root.id
            flush()
        }

        @DocumentResponse
        @Test
        fun `Given valid request, should return folderResponseDTO`() {
            grantReadPermission(contentService.getFolder(rootFolderId))
            contentService.createFolder("child1", rootFolderId)
            contentService.createPage(rootFolderId, "child2", "child2 content")
            flush()

            mockMvc.get(
                path = getFolderRequest(rootFolderId),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isSuccess()
                responseJsonPath("$.folder.name").equalsValue("root")
                responseJsonPath("$.folder.id").equalsLong(rootFolderId)
                responseJsonPath("$.folder.children").isNotEmpty()
            }
        }

        @Test
        fun `Given valid request and page child, should return folderResponseDTO with page child`() {
            grantReadPermission(contentService.getFolder(rootFolderId))
            val pageChild = contentService.createPage(rootFolderId, "childPage", "childPageContent")
            flush()

            mockMvc.get(
                path = getFolderRequest(rootFolderId),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isSuccess()
                responseJsonPath("$.folder.name").equalsValue("root")
                responseJsonPath("$.folder.id").equalsLong(rootFolderId)
                responseJsonPath("$.folder.children[0].name").equalsValue("childPage")
                responseJsonPath("$.folder.children[0].id").equalsLong(pageChild.id)
            }
        }

        @Test
        fun `Given valid request and folder child, should return folderResponseDTO with folder child`() {
            grantReadPermission(contentService.getFolder(rootFolderId))
            val folderChild = contentService.createFolder("childFolder", rootFolderId)
            flush()

            mockMvc.get(
                path = getFolderRequest(rootFolderId),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isSuccess()
                responseJsonPath("$.folder.name").equalsValue("root")
                responseJsonPath("$.folder.id").equalsLong(rootFolderId)
                responseJsonPath("$.folder.children[0].name").equalsValue("childFolder")
                responseJsonPath("$.folder.children[0].id").equalsLong(folderChild.id)
                responseJsonPath("$.folder.children[0].children").isEmpty()
            }
        }

        @Test
        fun `Given invalid folder id, should return 404`() {
            grantReadPermission(contentService.getFolder(rootFolderId))

            mockMvc.get(
                path = getFolderRequest(rootFolderId + 1),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isError(HttpStatus.NOT_FOUND)
            }
        }

        @Test
        fun `Given missing READ permission, should return 401`() {
            mockMvc.get(
                path = getFolderRequest(rootFolderId),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
        }
    }

    @Transactional
    @OrderTests
    @Nested
    inner class GetFolderTree() : MockMvcTestBase() {
        private fun getTreeRequest(id: Long) = Path("/api/content/folder/$id/tree")

        private var rootFolderId: Long = -1

        @BeforeEach
        fun setup() {
            val root = Folder(false, mutableSetOf(), "root", null, null)
            contentService.createFolder(root)
            rootFolderId = root.id
        }

        @DocumentResponse
        @Test
        fun `Given valid request, should return folderResponseDTO with nested children`() {
            grantReadPermission(contentService.getFolder(rootFolderId))
            val childFolder = contentService.createFolder("childFolder", rootFolderId)
            val childPage = contentService.createPage(childFolder.id, "childPage", "childPageContent")
            flush()

            mockMvc.get(
                path = getTreeRequest(rootFolderId),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isSuccess()
                responseJsonPath("$.folder.id").equalsLong(rootFolderId)
                responseJsonPath("$.folder.name").equalsValue("root")
                responseJsonPath("$.folder.children[0].id").equalsLong(childFolder.id)
                responseJsonPath("$.folder.children[0].name").equalsValue(childFolder.name)
                responseJsonPath("$.folder.children[0].children[0].name").equalsValue(childPage.name)
                responseJsonPath("$.folder.children[0].children[0].id").equalsLong(childPage.id)
            }
        }

        @Test
        fun `Given invalid folder id, should return 404`() {
            grantReadPermission(contentService.getFolder(rootFolderId))
            mockMvc.get(
                path = getTreeRequest(rootFolderId + 1),
                headers = HttpHeaders().authorization(getAccessToken())
            ) {
                isError(HttpStatus.NOT_FOUND)
            }
        }

        @Test
        fun `Given missing READ permission, should return 401`() {
            mockMvc.get(
                path = getTreeRequest(rootFolderId),
                headers = HttpHeaders()
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
        }
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
        fun `Given invalid parentId, should return 404`() {
            grantWritePermission(parent)
            mockMvc.put(
                path = createFolderRequestPath,
                body = mapper.writeValueAsString(CreateFolderDTO("testFolder", 999)),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.NOT_FOUND)
            }
        }

        @Test
        fun `Given page as parentId, should return 400`() {
            grantWritePermission(parent)
            val page = contentService.createPage(parent.id, "testPage", "testContent")
            flush()

            mockMvc.put(
                path = createFolderRequestPath,
                body = mapper.writeValueAsString(CreateFolderDTO("testFolder", page.id)),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
        }

        @Test
        fun `Given missing WRITE permission, should return 401`() {
            mockMvc.put(
                path = createFolderRequestPath,
                body = mapper.writeValueAsString(CreateFolderDTO("testFolder", parent.id)),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.UNAUTHORIZED)
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
        fun `Given invalid folderId, should return 404`() {
            grantWritePermission(root)
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + 999),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.NOT_FOUND)
            }
            flush()
            Assertions.assertEquals(1, contentService.getFolder(root.id).children.size)
        }

        @Test
        fun `Given missing WRITE permission, should return 401`() {
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + testFolder.id),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
            flush()
            Assertions.assertEquals(1, contentService.getFolder(root.id).children.size)
        }

        @Test
        fun `Given scope root, should return 400`() {
            grantWritePermission(root)
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + root.id),
                headers = HttpHeaders().contentTypeJson().authorization(getAccessToken())
            ) {
                isError(HttpStatus.BAD_REQUEST)
            }
            flush()
            Assertions.assertEquals(1, contentService.getFolder(root.id).children.size)
        }

        @Test
        fun `Given not authenticated user, should return 401`() {
            mockMvc.delete(
                path = Path(deleteFolderRequestPath + testFolder.id),
                headers = HttpHeaders().contentTypeJson()
            ) {
                isError(HttpStatus.UNAUTHORIZED)
            }
            flush()
            Assertions.assertEquals(1, contentService.getFolder(root.id).children.size)
        }
    }


}