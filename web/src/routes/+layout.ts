import type { LayoutLoad } from './$types';

export const ssr = false;

export const load = (() => {
  return {
    meta: {
      title: 'UFTOS',
    },
  };
}) satisfies LayoutLoad;
