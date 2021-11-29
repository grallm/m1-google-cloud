import type { NextPage } from 'next'
import Head from 'next/head'
import { Button, Container, Form } from 'react-bootstrap'

const PostPage: NextPage = () => {
  return (
    <Container className=' bg-white border p-3 rounded'>
      <Head>
        <title>InstaCrash - Poster</title>
      </Head>

      <Form>
        <h3 className='mb-3'>Ajouter un poste</h3>
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
    </Container>
  )
}

export default PostPage
