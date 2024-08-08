import { loadSubjects } from '$lib/utils/resources';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async () => {
  try {
    return {
      initialData: await loadSubjects('', ''),
      meta: {
        title: 'Fächer',
      },
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}) satisfies PageLoad;
