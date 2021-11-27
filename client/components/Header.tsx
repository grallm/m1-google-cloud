import { faHome, faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import React from 'react'
import { Container, Nav, Navbar } from 'react-bootstrap'
import Link from 'next/link'
import { useRouter } from 'next/dist/client/router'

const Header: React.FC<{}> = () => {
  const router = useRouter()

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
            <Link href='/post' passHref>
              <Nav.Link active={router.pathname === '/post'}>
                <FontAwesomeIcon icon={faPlus} /> Poster
              </Nav.Link>
            </Link>
          </Nav>

          {/* Right */}
          <div className='justify-content-end'>
            <Navbar.Text>
              USERNAME
            </Navbar.Text>
          </div>
        </div>
      </Container>
    </Navbar>
  )
}

export default Header
