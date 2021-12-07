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
    <Navbar className="border-bottom fixed-top bg-white" expand="sm">
      <Container>
        <Link href='/' passHref>
          <Navbar.Brand>
            InstaCrash
          </Navbar.Brand>
        </Link>

        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
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
          <Navbar.Text className='p-0'>
            {
              session?.user?.name
                ? (
                  <NavDropdown title={session.user.name}>
                    <NavDropdown.Item href={session.user.userId || '/'}>Mon Profil</NavDropdown.Item>
                    <NavDropdown.Divider />
                    <NavDropdown.Item>
                      <Button variant="outline-danger" onClick={() => signOut()}>Se déconnecter</Button>
                    </NavDropdown.Item>
                  </NavDropdown>
                )
                : <Button variant="outline-primary" onClick={() => setShow(true)}>Connexion</Button>
            }
          </Navbar.Text>
        </Navbar.Collapse>
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
