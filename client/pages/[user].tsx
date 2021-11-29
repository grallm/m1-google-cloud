import type { NextPage } from 'next'
import { useRouter } from 'next/router'
import Head from 'next/head'
import { useEffect, useState } from 'react'
import { Container, Spinner } from 'react-bootstrap'
import Post from '../components/Post'
import { PostEntity } from '../entities/Post.entity'
import { getAllPosts } from '../utils/post.api'

const User: NextPage = () => {
  const router = useRouter()
  const { user } = router.query

  const [posts, setPosts] = useState<PostEntity[] | null>(null)

  /**
   * Fetch all posts
   */
  useEffect(() => {
    getAllPosts()
      .then(posts => {
        setPosts(posts)
      })
  }, [])

  return (
    <Container className=' bg-white border p-3 rounded'>
      <Head>
        <title>InstaCrash - {user}</title>
      </Head>

      <h3 className='mb-3'>{user}</h3>

      {
        !posts
          ? (
            <div className='w-100 d-flex justify-content-center mt-5'>
              <Spinner animation="border" variant='warning' style={{ width: '50px', height: '50px' }} />
            </div>
          )
          : posts.map((post, i) => (
            <div key={i}>
              <Post
                owner={post.owner}
                image={post.image}
                description={post.body}
              />
            </div>
          ))
      }
    </Container>
  )
}

export default User
