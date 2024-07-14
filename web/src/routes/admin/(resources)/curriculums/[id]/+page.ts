import { getCurriculum } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  try {
    const curriculum = await getCurriculum(params.id);
    return {
      curriculum,
      meta: {
        title: `Curriculum — ${curriculum.name}`,
      },
    };
  } catch {
    error(404, { message: `Curriculum with id ${params.id} not found` });
  }
}) satisfies PageLoad;
