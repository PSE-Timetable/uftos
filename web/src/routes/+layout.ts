import type { LayoutLoad, PageLoad } from './$types';

export const load = (async () => {
  return {
    meta: {
      title: 'UFTOS',
    },
  };
}) satisfies LayoutLoad;
