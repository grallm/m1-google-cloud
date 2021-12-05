import type { NextPage } from 'next'
import { useRouter } from 'next/router'
import Head from 'next/head'
import { useEffect, useState } from 'react'
import { Button, Container, Spinner } from 'react-bootstrap'
import Post from '../components/Post'
import { PostEntity } from '../entities/Post.entity'
import { getUserPosts, getUser, followUser, unfollowUser } from '../utils/user.api'
import { UserEntity } from '../entities/User.entity'
import { useSession } from 'next-auth/client'

const User: NextPage = () => {
  const router = useRouter()
  const { user } = router.query
  const [session] = useSession()

  const [posts, setPosts] = useState<PostEntity[] | null>(null)
  const [loadingUser, setLoadingUser] = useState(true)
  const [userState, setUserState] = useState<UserEntity | null>(null)
  const [followsUser, setFollowsUser] = useState(false)

  /**
   * Fetch all posts
   */
  useEffect(() => {
    // Wait for param to be loaded
    if (typeof user === 'string') {
      // Load user profile
      getUser(user)
        .then((user) => {
          setUserState(user)
          setLoadingUser(false)
        })
        .catch(() => setLoadingUser(false))

      // Load user's posts
      getUserPosts(user).then((posts) => {
        setPosts(posts)
      })
    }
  }, [user])

  /**
   * Check if follows
   */
  useEffect(() => {
    console.log(session)
    if (session?.user && user) {
      setFollowsUser(!!session.user.listFollowing.find(follows => follows === user))
    }
  }, [session, user])

  return (
    <Container className="p-3">
      <Head>
        <title>InstaCrash - {userState?.name || 'Profil'}</title>
      </Head>

      <div className='bg-white border rounded mb-3 p-3'>
        {loadingUser
          ? (
            <div className="w-100 d-flex justify-content-center mt-5">
              <Spinner
                animation="border"
                variant="warning"
                style={{ width: '50px', height: '50px' }}
              />
            </div>
          )
          : userState
            ? (
              <div className='d-flex align-items-center'>
                <h3 className='m-0 me-3'>{user}</h3>
                <Button
                  variant={followsUser ? 'outline-primary' : 'primary'}
                  onClick={() => {
                    setFollowsUser(!followsUser)

                    if (session?.user?.accessToken && typeof user === 'string') {
                      followsUser ? unfollowUser(user, session.user.accessToken) : followUser(user, session.user.accessToken)
                    }
                  }}
                >{followsUser ? 'Abonné' : 'S\'abonner'}</Button>
              </div>
            )
            : (
              <div className='p-3 d-flex justify-content-center'>Cet utilisateur n'existe pas</div>
            )
        }
      </div>

      {posts?.map((post, i) => (
        <div key={i}>
          <Post
            owner={post.owner}
            image={post.url}
            description={post.body}
          />
        </div>
      ))
      }
    </Container>
  )
}

export default User
