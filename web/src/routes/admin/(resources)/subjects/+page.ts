import { loadSubjects } from '$lib/utils/resources';
import type { PageLoad } from './$types';

export const load = (async () => {
  return {
    initialData: await loadSubjects('', ''),
    meta: {
      title: 'Fächer',
    },
  };
}) satisfies PageLoad;
