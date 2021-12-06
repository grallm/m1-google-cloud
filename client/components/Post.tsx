import React, { useEffect, useState } from 'react'
import { Card } from 'react-bootstrap'
import Image from 'next/image'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHeart as farHeart } from '@fortawesome/free-regular-svg-icons'
import { faHeart as fasHeart } from '@fortawesome/free-solid-svg-icons'
import Link from 'next/link'
import { PostEntity } from '../entities/Post.entity'
import { useSession } from 'next-auth/client'
import { doesLike, likePost, unlikePost } from '../utils/post.api'

interface Props {
  post: PostEntity
  showSigninAlert: () => void
}
const Post: React.FC<Props> = ({ post, showSigninAlert }) => {
  const postId = post.id
  const { ownerId, owner, date, body, url } = post
  const [nbLikes, setNbLikes] = useState(parseInt(post.likes))

  const [session, loadingSession] = useSession()

  const [loadedLike, setLoadedLike] = useState(false)
  const [liked, setLiked] = useState(false)

  /**
   * Like/Unlike the post when clicking the heart
   */
  const likePostClick = () => {
    if (!session) {
      showSigninAlert()
    } else if (session.user && loadedLike) {
      // Edit local count
      setNbLikes(liked
        ? nbLikes - 1
        : nbLikes + 1
      )

      setLiked(!liked)

      const likeAction = liked ? unlikePost(postId, session.user.accessToken) : likePost(postId, session.user.accessToken)
      likeAction
        .then(success => {
          if (!success) {
            // Rollback if fail
            setNbLikes(liked
              ? nbLikes - 1
              : nbLikes + 1
            )

            setLiked(!liked)
          }
        })
        .catch(() => setLiked(!liked))
    }
  }

  /**
   * Check if the post is liked or not
   */
  useEffect(() => {
    if (session?.user) {
      const checkIfLikes = async () => {
        if (session.user) {
          const hasLiked = await doesLike(postId, session.user.accessToken)

          setLiked(hasLiked)
          setLoadedLike(true)
        }
      }
      checkIfLikes()
    } else if (!loadingSession) {
      // When session loaded, if disconnected, enable like button
      setLoadedLike(true)
    }
  }, [loadingSession, postId, session?.user])

  const formatDate = (timestamp: string) => {
    const date = new Date(parseInt(timestamp))

    const min = date.getMinutes()
    return `${date.toLocaleDateString('fr')} ${date.getHours()}:${min < 10 ? `0${min}` : min}`
  }

  return (
    <Card className="mb-4">
      <Link href={ownerId} passHref>
        <Card.Title role='button' className="p-3 pb-2 d-flex justify-content-between align-items-baseline">{owner}<div className=' fs-6'>{formatDate(date)}</div></Card.Title>
      </Link>

      <Image
        src={url}
        alt="Image"
        layout="responsive"
        width={100}
        height={100}
        objectFit="cover"
        className='border-top border-bottom'
      />

      <Card.Body>
        <div className='d-flex mb-1'>
          <a onClick={likePostClick}><FontAwesomeIcon
            icon={liked ? fasHeart : farHeart}
            color={loadedLike ? (liked ? 'red' : '') : 'lightgray'}
            style={{ width: '25px', height: '25px' }}
          /></a>
          <div className='ms-3'><b>{nbLikes}</b> j'aimes</div>
        </div>

        <Card.Text>
          <Link href={owner} passHref><b role='button'>{owner}</b></Link> {body}
        </Card.Text>
      </Card.Body>
    </Card>
  )
}

export default Post
