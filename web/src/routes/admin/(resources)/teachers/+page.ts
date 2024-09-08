import { loadTeacherPage } from '$lib/utils/resources';
import { init } from '$lib/utils/server';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
  init(fetch);
  try {
    return {
      initialData: await loadTeacherPage('', '', 0, 15),
      meta: {
        title: 'Lehrer',
      },
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}) satisfies PageLoad;
