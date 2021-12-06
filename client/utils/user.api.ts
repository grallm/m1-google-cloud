import { PostEntity } from '../entities/Post.entity'
import { UserEntity } from '../entities/User.entity'
import { ApiEntity, apiRoute, EntityList } from './common.api'

/**
 * Fetch all user's
 * @returns
 */
export const getAllUsers = async (): Promise<UserEntity[]> => {
  try {
    const res = await fetch(`${apiRoute}/user`)

    if (!res.ok) throw await res.json()

    const users = (await res.json()) as EntityList<UserEntity>
    console.log(users)

    return users?.items.map(user => ({
      ...user.properties,
      id: user.key.name
    })) || []
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return []
  }
}

/**
 * Get data of a specific User
 * @param userId
 * @returns
 */
export const getUser = async (userId: string): Promise<UserEntity | null> => {
  try {
    const res = await fetch(`${apiRoute}/user/${userId}`)

    if (!res.ok) throw await res.json()

    const user = (await res.json()) as ApiEntity<UserEntity>

    return user
      ? {
        ...user.properties,
        id: user.key.name
      } as UserEntity
      : null
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return null
  }
}

/**
 * Fetch all user's posts
 * @param userId
 * @returns Formatted Posts
 */
export const getUserPosts = async (userId: string): Promise<PostEntity[]> => {
  try {
    const res = await fetch(`${apiRoute}/user/${userId}/posts`)
    const posts = (await res.json()) as EntityList<PostEntity>

    return posts?.items.map((post) => ({
      id: post.key.name,
      ...post.properties
    })) || []
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return []
  }
}

/**
 * Follow a user
 * @param userId user to follow's ID
 * @param accessToken Google Access token of logged in user
 * @returns
 */
export const followUser = async (userId: string, accessToken: string): Promise<boolean> => {
  try {
    const res = await fetch(`${apiRoute}/user/${userId}/follow?access_token=${accessToken}`, {
      method: 'POST'
    })
    await res.json()

    return res.ok
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return false
  }
}

/**
 * Unfollow a user
 * @param userId user to follow's ID
 * @param accessToken Google Access token of logged in user
 * @returns
 */
export const unfollowUser = async (userId: string, accessToken: string): Promise<boolean> => {
  try {
    const res = await fetch(`${apiRoute}/user/${userId}/unfollow?access_token=${accessToken}`, {
      method: 'DELETE'
    })
    await res.json()

    return res.ok
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return false
  }
}
