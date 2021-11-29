import type { NextPage } from 'next'
import Head from 'next/head'
import { Button, Container, Form } from 'react-bootstrap'
import { useSession } from 'next-auth/client'
import Login from '../components/Login'

const PostPage: NextPage = () => {
  const [session] = useSession()

  return (
    <Container className='bg-white border p-3 rounded'>
      <Head>
        <title>InstaCrash - Poster</title>
      </Head>

      <h3 className='mb-3'>Ajouter un poste</h3>
      {
        !session
          ? (
            <div>
              <div className='mb-2'>
              Vous devez être connecté pour continuer :
              </div>
              <Login />
            </div>
          )
          : (
            <Form>
              <Form.Group controlId="formFileLg" className="mb-3">
                <Form.Label>Photo</Form.Label>
                <Form.Control type="file" size="lg" placeholder='test' />
              </Form.Group>

              <Form.Group className="mb-3" controlId="exampleForm.ControlTextarea1">
                <Form.Label>Description</Form.Label>
                <Form.Control placeholder='Comment se passe votre vie ?' as="textarea" rows={3} />
              </Form.Group>

              <Form.Group className='d-flex justify-content-end mt-4'>
                <Button type="submit" size='lg'>Partager</Button>
              </Form.Group>
            </Form>
          )
      }
    </Container>
  )
}

export default PostPage
