import type { NextPage } from 'next'
import Head from 'next/head'
import { Button, Container, Form } from 'react-bootstrap'
import { useSession } from 'next-auth/client'
import Login from '../components/Login'
import { ChangeEvent, FormEvent, useState } from 'react'
import { apiRoute } from '../utils/common.api'
import { useRouter } from 'next/router'

const getBase64 = (file: File) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}

const PostPage: NextPage = () => {
  const [session] = useSession()
  const router = useRouter()

  const [descInput, setDescInput] = useState('')
  const [fileInput, setFileInput] = useState<File | null>(null)

  const [submitting, setSubmitting] = useState(false)

  /**
   * Post submission
   */
  const submitPost = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    if (fileInput && session?.user) {
      const submitPostFetch = async () => {
        if (session?.user) {
          const fileBase64 = await getBase64(fileInput)

          try {
            const res = await fetch(`${apiRoute}/post?access_token=${session.user.accessToken}`, {
              method: 'POST',
              body: JSON.stringify({
                owner: session.user.name || 'NAME',
                image: fileBase64,
                description: descInput
              })
            })

            if (!res.ok) throw new Error()

            // Success, redirect to profile
            router.push(session.user.userId || '/')
          } catch (error) {
            // eslint-disable-next-line no-console
            console.error(error)
            setSubmitting(false)
          }
        }
      }

      setSubmitting(true)
      submitPostFetch()
    }
  }

  return (
    <div className='px-3'>
      <Container className='bg-white border p-3 rounded' style={{ maxWidth: '768px' }}>
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
                <Login callbackUrl='/post' />
              </div>
            )
            : (
              <Form onSubmit={submitPost}>
                <Form.Group controlId="formFileLg" className="mb-3">
                  <Form.Label>Photo</Form.Label>
                  <Form.Control
                    className='d-none d-sm-block'
                    type="file"
                    size="lg"
                    placeholder='test'
                    accept='image/gif, image/jpeg, image/png'
                    onChange={(e) => {
                      const inputEvent = e as ChangeEvent<HTMLInputElement>
                      if (inputEvent.target.files) {
                        setFileInput(inputEvent.target.files[0])
                      }
                    }}
                  />
                  <Form.Control
                    className='d-sm-none d-block'
                    type="file"
                    placeholder='test'
                    accept='image/gif, image/jpeg, image/png'
                    onChange={(e) => {
                      const inputEvent = e as ChangeEvent<HTMLInputElement>
                      if (inputEvent.target.files) {
                        setFileInput(inputEvent.target.files[0])
                      }
                    }}
                  />
                </Form.Group>

                <Form.Group className="mb-3" controlId="exampleForm.ControlTextarea1">
                  <Form.Label>Description</Form.Label>
                  <Form.Control
                    placeholder='Comment se passe votre vie ?'
                    as="textarea"
                    rows={3}
                    value={descInput}
                    onChange={(e) => setDescInput(e.target.value)}
                  />
                </Form.Group>

                <Form.Group className='d-flex justify-content-end mt-4'>
                  <Button
                    type="submit"
                    size='lg'
                    disabled={submitting}
                  >Partager</Button>
                </Form.Group>
              </Form>
            )
        }
      </Container>
    </div>
  )
}

export default PostPage
