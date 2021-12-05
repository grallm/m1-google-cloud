export const apiRoute = process.env.NEXT_PUBLIC_API_URL || ''

type DatastoreEntity<E> = {
  key: {
    kind: string;
    appId: string;
    id: string;
    name: string;
    complete: boolean;
    namespace: string;
  };
  appId: string;
  kind: string;
  namespace: string;
  properties: Omit<E, 'id'>;
}

export type EntityList<E> = {
  items: DatastoreEntity<E>[];
} | null

export type ApiEntity<E> = DatastoreEntity<E> | null
