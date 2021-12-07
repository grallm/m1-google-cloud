import { faGoogle } from '@fortawesome/free-brands-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import type { NextPage } from 'next'
import { signIn, useSession } from 'next-auth/client'
import Head from 'next/head'
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { Button, Container, Modal } from 'react-bootstrap'
import PostsList from '../components/PostsList'
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
      <PostsList showSigninAlert={() => setShowSigninAlert(true)} posts={posts} />

      {/* Discover */}
      {
        discoverPosts && discoverPosts.length > 0 && (
          <div className='mt-5'>
            <h3 className='mb-3'>Découvrir plus</h3>

            <PostsList showSigninAlert={() => setShowSigninAlert(true)} posts={discoverPosts} />
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

export default Home
