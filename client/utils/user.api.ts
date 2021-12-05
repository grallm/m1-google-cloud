import { PostEntity } from '../entities/Post.entity'
import { UserEntity } from '../entities/User.entity'
import { ApiEntity, apiRoute, EntityList } from './common.api'

/**
 * Fetch all user's posts
 * @param userId
 * @returns Formatted Posts
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
      id: post.key.id,
      owner: post.properties.owner,
      date: post.properties.date,
      body: post.properties.body,
      url: post.properties.url
    })) || []
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return []
  }
}

export const followUser = async (userId: string, accessToken: string): Promise<boolean> => {
  try {
    const res = await fetch(`${apiRoute}/user/${userId}/follow?access_token=${accessToken}`)
    await res.json()

    return res.ok
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return false
  }
}

export const unfollowUser = async (userId: string, accessToken: string): Promise<boolean> => {
  try {
    const res = await fetch(`${apiRoute}/user/${userId}/unfollow?access_token=${accessToken}`)
    await res.json()

    return res.ok
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return false
  }
}
