import { faGoogle } from '@fortawesome/free-brands-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import type { NextPage } from 'next'
import { signIn, useSession } from 'next-auth/client'
import Head from 'next/head'
import router from 'next/router'
import { useEffect, useState } from 'react'
import { Button, Container, Modal, Spinner } from 'react-bootstrap'
import Post from '../components/Post'
import { PostEntity } from '../entities/Post.entity'
import { getAllPosts } from '../utils/post.api'

const Home: NextPage = () => {
  const [session, loading] = useSession()

  const [posts, setPosts] = useState<PostEntity[] | null>(null)
  const [showSigninAlert, setShowSigninAlert] = useState(false)

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
              <Post
                post={post}
                showSigninAlert={() => setShowSigninAlert(true)}
              />
            </div>
          ))
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
