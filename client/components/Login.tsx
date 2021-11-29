import { faGoogle } from '@fortawesome/free-brands-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { useSession, signIn } from 'next-auth/client'
import React, { useEffect, useState } from 'react'
import { Button, Modal, Spinner } from 'react-bootstrap'
import { addUser } from '../utils/user.api'

interface Props {
  callbackUrl?: string
}
const Login: React.FC<Props> = ({ callbackUrl }) => {
  const [session, loading] = useSession()

  const [show, setShow] = useState(false)

  useEffect(() => {
    // Add user to Datastore
    if (session) {
      addUser(session)
    }
  }, [session])

  if (session) return null

  return (
    <>
      {
        loading
          ? (
            <div className='w-100 d-flex justify-content-center'>
              <Spinner animation="border" variant='warning' style={{ width: '50px', height: '50px' }} />
            </div>
          )
          : <Button variant="outline-primary" onClick={() => setShow(true)}>Connexion</Button>
      }
      {/* Auth modal */}
      <Modal
        show={show}
        onHide={() => setShow(false)}
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
            onClick={() => signIn('google', { callbackUrl: callbackUrl || '/' })}
          ><FontAwesomeIcon icon={faGoogle} /> Connexion avec Google</Button>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" onClick={() => setShow(false)}>
            Annuler
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  )
}

export default Login
