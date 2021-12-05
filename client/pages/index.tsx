import type { NextPage } from 'next'
import { useSession } from 'next-auth/client'
import Head from 'next/head'
import { useEffect, useState } from 'react'
import { Container, Spinner } from 'react-bootstrap'
import Post from '../components/Post'
import { PostEntity } from '../entities/Post.entity'
import { getAllPosts } from '../utils/post.api'

const Home: NextPage = () => {
  const [session, loading] = useSession()

  const [posts, setPosts] = useState<PostEntity[] | null>(null)

  /**
   * Fetch all posts
   */
  useEffect(() => {
    if (!loading) {
      getAllPosts(session?.user?.accessToken || null)
        .then(posts => {
          setPosts(posts)
        })
    }
  }, [loading, session])

  return (
    <Container>
      <Head>
        <title>InstaCrash - Feed</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>

      {
        !posts
          ? (
            <div className='w-100 d-flex justify-content-center mt-5'>
              <Spinner animation="border" variant='warning' style={{ width: '50px', height: '50px' }} />
            </div>
          )
          : posts.map((post, i) => (
            <div key={i}>
              <Post post={post} />
            </div>
          ))
      }
    </Container>
  )
}

export default Home
