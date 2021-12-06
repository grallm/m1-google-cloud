export interface PostEntity {
  id: string
  ownerId: string
  owner: string
  date: string
  body: string
  url: string
  likes: string
}

export interface PostTimelineList {
  items: PostTimeline[]
}
export interface PostTimeline {
  ownerId: string;
  owner: string;
  description: string;
  date: string;
  likes: string;
  image: string;
}
