import { faChild, faHome, faPlus } from '@fortawesome/free-solid-svg-icons'
import { faGoogle } from '@fortawesome/free-brands-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import React, { useState } from 'react'
import { Button, Container, Modal, Nav, Navbar, NavDropdown } from 'react-bootstrap'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { signIn, signOut, useSession } from 'next-auth/client'

const Header: React.FC<{}> = () => {
  const router = useRouter()
  const [session] = useSession()

  const [show, setShow] = useState(false)

  return (
    <Navbar className="border-bottom fixed-top bg-white">
      <Container>
        <Link href='/' passHref>
          <Navbar.Brand>
            InstaCrash
          </Navbar.Brand>
        </Link>

        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerDemo01" aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="navbar-collapse collapse">
          <Nav className="me-auto">
            {/* Feed */}
            <Link href='/' passHref>
              <Nav.Link active={router.pathname === '/'}>
                <FontAwesomeIcon icon={faHome} /> Feed
              </Nav.Link>
            </Link>

            {/* Post */}
            {
              session && (
                <Link href='/post' passHref>
                  <Nav.Link active={router.pathname === '/post'}>
                    <FontAwesomeIcon icon={faPlus} /> Poster
                  </Nav.Link>
                </Link>
              )
            }

            <Link href='/encounter' passHref>
              <Nav.Link active={router.pathname === '/encounter'}>
                <FontAwesomeIcon icon={faChild} /> Rencontrer
              </Nav.Link>
            </Link>
          </Nav>

          {/* Right */}
          <div className='justify-content-end'>
            <Navbar.Text>
              {
                session?.user?.name
                  ? (
                    <NavDropdown title={session.user.name}>
                      <NavDropdown.Item href={session.user.email || '/'}>Mon Profil</NavDropdown.Item>
                      <NavDropdown.Divider />
                      <NavDropdown.Item>
                        <Button variant="outline-danger" onClick={() => signOut()}>Se déconnecter</Button>
                      </NavDropdown.Item>
                    </NavDropdown>
                  )
                  : <Button variant="outline-primary" onClick={() => setShow(true)}>Connexion</Button>
              }
            </Navbar.Text>
          </div>
        </div>
      </Container>

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
            onClick={() => signIn('google', { callbackUrl: router.asPath })}
          ><FontAwesomeIcon icon={faGoogle} /> Connexion avec Google</Button>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" onClick={() => setShow(false)}>
            Annuler
          </Button>
        </Modal.Footer>
      </Modal>
    </Navbar>
  )
}

export default Header
