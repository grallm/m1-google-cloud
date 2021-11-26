import { faPlus } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import React from "react"
import { Container, Nav, Navbar } from 'react-bootstrap'

const Header: React.FC<{}> = () => {
  return (
    <Navbar className="border-bottom">
      <Container>
        <Navbar.Brand>
          InstaCrash
        </Navbar.Brand>
        
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerDemo01" aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="navbar-collapse collapse">
          <Nav className="me-auto">
            <Nav.Link className="fs-6">
              <FontAwesomeIcon icon={faPlus} /> Poster
            </Nav.Link>
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