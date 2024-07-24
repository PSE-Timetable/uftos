import { getGrade } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  try {
    const grade = await getGrade(params.id);
    return {
      grade,
      meta: {
        title: `Stufe — ${grade.name}`,
      },
    };
  } catch {
    error(404, { message: `Grade with id ${params.id} not found` });
  }
}) satisfies PageLoad;
