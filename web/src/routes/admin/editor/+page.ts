import type { PageLoad } from '../../$types';

export const load = (() => {
  return {
    meta: {
      title: 'UCDL Editor',
    },
  };
}) satisfies PageLoad;
