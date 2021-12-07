import { faGoogle } from '@fortawesome/free-brands-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import type { NextPage } from 'next'
import { signIn, useSession } from 'next-auth/client'
import Head from 'next/head'
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { Button, Container, Modal, Spinner } from 'react-bootstrap'
import Post from '../components/Post'
import { PostEntity } from '../entities/Post.entity'
import { getAllPosts, getTimeline } from '../utils/post.api'

const Home: NextPage = () => {
  const [session, loading] = useSession()
  const router = useRouter()

  const [posts, setPosts] = useState<PostEntity[] | null>(null)
  const [discoverPosts, setDiscoverPosts] = useState<PostEntity[] | null>(null)
  const [showSigninAlert, setShowSigninAlert] = useState(false)

  /**
   * Fetch all posts
   */
  useEffect(() => {
    if (!loading) {
      getAllPosts()
        .then(posts => {
          // If logged in, add in discover
          session?.user
            ? setDiscoverPosts(posts.slice(0, 20))
            : setPosts(posts.slice(0, 20))
        })

      // Load timeline if connected
      if (session?.user) {
        getTimeline(session.user.accessToken)
          .then(posts => {
            setPosts(posts)
          })
      }
    }
  }, [loading, session])

  return (
    <Container>
      <Head>
        <title>InstaCrash - Feed</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>

      {/* Timeline if logged in, else all posts */}
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
                post={post}
                showSigninAlert={() => setShowSigninAlert(true)}
              />
            </div>
          ))
      }

      {/* Discover */}
      {
        discoverPosts && discoverPosts.length > 0 && (
          <div className='mt-5'>
            <h3 className='mb-3'>Découvrir plus</h3>
            {
              discoverPosts?.map((post, i) => (
                <div key={i}>
                  <Post
                    post={post}
                    showSigninAlert={() => setShowSigninAlert(true)}
                  />
                </div>
              ))
            }
          </div>
        )
      }

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
            onClick={() => signIn('google', { callbackUrl: 'http://localhost:3000/' + router.asPath })}
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

export default Home
