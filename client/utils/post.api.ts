import { PostEntity, PostTimelineList } from '../entities/Post.entity'
import { apiRoute, EntityList } from './common.api'

/**
 * Fetch all posts
 * @param token Goole Access Token (optional)
 * @returns Formatted Posts
 */
export const getAllPosts = async (): Promise<PostEntity[]> => {
  try {
    const res = await fetch(
      `${apiRoute}/post`
    )

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
 * Fetch all posts
 * @param token Goole Access Token (optional)
 * @returns Formatted Posts
 */
export const getTimeline = async (token: string): Promise<PostEntity[]> => {
  try {
    const res = await fetch(
      `${apiRoute}/post/timeLine` + (token ? `?access_token=${token}` : '')
    )

    const posts = (await res.json()) as PostTimelineList

    return posts?.items.map((post) => ({
      id: 'post',
      body: 'body',
      url: post.image,
      ...post
    })) || []
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return []
  }
}

/**
 * Check if likes a post
 * @param postId
 * @param accessToken
 * @returns
 */
export const doesLike = async (postId: string, accessToken: string): Promise<boolean> => {
  try {
    const res = await fetch(`${apiRoute}/like/${postId}?access_token=${accessToken}`, {
      method: 'GET'
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
 * Like a post
 * @param postId
 * @param accessToken
 * @returns
 */
export const likePost = async (postId: string, accessToken: string): Promise<boolean> => {
  try {
    const res = await fetch(`${apiRoute}/like/${postId}?access_token=${accessToken}`, {
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
 * Remove a like
 * @param postId
 * @param accessToken
 * @returns
 */
export const unlikePost = async (postId: string, accessToken: string): Promise<boolean> => {
  try {
    const res = await fetch(`${apiRoute}/like/${postId}?access_token=${accessToken}`, {
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
