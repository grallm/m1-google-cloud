import type { NextPage } from 'next'
import { useRouter } from 'next/router'
import Head from 'next/head'
import { useEffect, useState } from 'react'
import { Button, Container, Modal, Spinner } from 'react-bootstrap'
import { PostEntity } from '../entities/Post.entity'
import { getUserPosts, getUser, followUser, unfollowUser } from '../utils/user.api'
import { UserEntity } from '../entities/User.entity'
import { signIn, useSession } from 'next-auth/client'
import { faGoogle } from '@fortawesome/free-brands-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import PostsList from '../components/PostsList'

const User: NextPage = () => {
  const router = useRouter()
  const { user } = router.query
  const [session] = useSession()

  const [showSigninAlert, setShowSigninAlert] = useState(false)
  const [posts, setPosts] = useState<PostEntity[] | null>(null)
  const [loadingUser, setLoadingUser] = useState(true)
  const [userState, setUserState] = useState<UserEntity | null>(null)
  const [followsUser, setFollowsUser] = useState(false)

  const followUserClick = () => {
    // Connected
    if (session) {
      setFollowsUser(!followsUser)

      if (session?.user?.accessToken && typeof user === 'string') {
        (followsUser ? unfollowUser(user, session.user.accessToken) : followUser(user, session.user.accessToken))
          .then(success => {
            // If didn't work, rollback
            if (!success) {
              setFollowsUser(!followsUser)
            } else {
              // If worked, reload to reload session
              router.reload()
            }
          })
          .catch(() => setFollowsUser(!followsUser))
      }
    } else {
      setShowSigninAlert(true)
    }
  }

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
            <div className="w-100 d-flex justify-content-center">
              <Spinner
                animation="border"
                variant="warning"
                style={{ width: '50px', height: '50px' }}
              />
            </div>
          )
          : userState
            ? (
              <div className='d-flex align-items-center justify-content-between'>
                <div className='d-flex align-items-center'>
                  <h3 className='m-0 me-3'>{userState.name}</h3>
                  {
                    userState.id === session?.user?.userId
                      ? <div className='text-black-50'>(Vous)</div>
                      : (
                        <Button
                          variant={followsUser ? 'outline-primary' : 'primary'}
                          onClick={followUserClick}
                        >{followsUser ? 'Se d??sabonner' : 'S\'abonner'}</Button>
                      )
                  }
                </div>

                <div>
                  <div><span className='fw-bold'>{userState.listFollowing?.length || 0}</span> abonnements</div>
                </div>
              </div>
            )
            : (
              <div className='p-3 d-flex justify-content-center'>Cet utilisateur n'existe pas</div>
            )
        }
      </div>

      <PostsList showSigninAlert={() => setShowSigninAlert(true)} posts={posts} />

      {/* Auth modal */}
      <Modal
        show={showSigninAlert}
        onHide={() => setShowSigninAlert(false)}
        backdrop="static"
        keyboard={false}
      >
        <Modal.Header closeButton>
          <Modal.Title>Connexion</Modal.Title>
        </Modal.Header>
        <Modal.Body className='d-flex justify-content-center'>
          <Button
            variant="outline-primary"
            size='lg'
            className='my-4'
            onClick={() => signIn('google', { callbackUrl: process.env.NEXT_PUBLIC_HOST + router.asPath })}
          ><FontAwesomeIcon icon={faGoogle} /> Connexion avec Google</Button>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" onClick={() => setShowSigninAlert(false)}>
            Annuler
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  )
}

export default User
