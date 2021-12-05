import { PostEntity } from '../entities/Post.entity'
import { apiRoute, EntityList } from './common.api'

/**
 * Fetch all posts
 * @param token Goole Access Token (optional)
 * @returns Formatted Posts
 */
export const getAllPosts = async (token?: string | null): Promise<PostEntity[]> => {
  try {
    const res = await fetch(
      `${apiRoute}/post` + (token ? `?access_token=${token}` : '')
    )

    const posts = (await res.json()) as EntityList<PostEntity>

    return posts?.items.map((post) => ({
      id: post.key.id,
      ...post.properties
    })) || []
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return []
  }
}
