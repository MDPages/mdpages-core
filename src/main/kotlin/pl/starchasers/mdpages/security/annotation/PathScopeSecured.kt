package pl.starchasers.mdpages.security.annotation

import pl.starchasers.mdpages.security.permission.PermissionType

/**
 * allows access to controller method only when user has all defined permissions for object defined in path parameter
 * @param value vararg, required permissions
 * @param pathParameterName Path parameter name, which contains secured object id
 *
 * @see ScopeSecured
 */
annotation class PathScopeSecured(vararg val value: PermissionType, val pathParameterName: String = "objectId")