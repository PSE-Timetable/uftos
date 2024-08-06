import { loadTags } from '$lib/utils/resources';
import type { PageLoad } from './$types';

export const load = (async () => {
  return {
    initialData: await loadTags('', ''),
    meta: {
      title: 'Tags',
    },
  };
}) satisfies PageLoad;
