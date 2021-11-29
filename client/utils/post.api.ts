import { PostEntity } from '../entities/Post.entity'
import { apiRoute, EntityList } from './common.api'

export interface ApiPost {
  key: {
    kind: string;
    appId: string;
    id: string;
    name: string;
    complete: boolean;
    namespace: string;
  };
  appId: string;
  kind: string;
  namespace: string;
  properties: {
    owner: string;
    date: string;
    body: string;
    url: string;
  };
}

/**
 * Fetch all posts
 * @returns Formatted Posts
 */
export const getAllPosts = async (token?: string | null): Promise<PostEntity[]> => {
  try {
    console.log(apiRoute)
    const res = await fetch(`${apiRoute}/post` + (token ? `?access_token=${token}` : ''))
    const posts = await res.json() as EntityList<ApiPost>

    return posts.items.map(post => ({
      id: post.key.id,
      owner: post.properties.owner,
      date: post.properties.date,
      body: post.properties.body,
      image: post.properties.url
    }))
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error)

    return []
  }
}
