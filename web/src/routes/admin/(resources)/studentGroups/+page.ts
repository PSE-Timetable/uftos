import type { PageLoad } from './$types';

export const load = (() => {
  return {
    meta: {
      title: 'Student Groups',
    },
  };
}) satisfies PageLoad;
