import { loadStudentPage } from '$lib/utils/resources';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async () => {
  try {
    return {
      initialData: await loadStudentPage('', '', 0, 15),
      meta: {
        title: 'Schüler',
      },
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}) satisfies PageLoad;
