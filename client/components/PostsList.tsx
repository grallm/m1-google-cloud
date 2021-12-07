import React from 'react'
import { Row, Spinner } from 'react-bootstrap'
import { PostEntity } from '../entities/Post.entity'
import Post from './Post'

interface Props {
  posts: PostEntity[] | null
  showSigninAlert: () => void
}
const PostsList: React.FC<Props> = ({ posts, showSigninAlert }) => {
  return (
    <Row md={2}>
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
                showSigninAlert={showSigninAlert}
              />
            </div>
          ))
      }
    </Row>
  )
}

export default PostsList
