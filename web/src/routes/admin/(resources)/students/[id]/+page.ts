import type { PageLoad } from '../$types';

export const load = (({ params }) => {
  return {
    id: params.id,
    meta: {
      title: 'Student',
    },
  };
}) satisfies PageLoad;
