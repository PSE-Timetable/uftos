import { loadGrades } from '$lib/utils/resources';
import { init } from '$lib/utils/server';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async () => {
  init();
  try {
    return {
      initialData: await loadGrades('', ''),
      meta: {
        title: 'Stufen',
      },
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}) satisfies PageLoad;
