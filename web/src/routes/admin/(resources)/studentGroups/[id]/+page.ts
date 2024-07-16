import { getStudentGroup } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  try {
    const studentGroup = await getStudentGroup(params.id);
    return {
      studentGroup,
      meta: {
        title: `Student Group — ${studentGroup.name}`,
      },
    };
  } catch {
    error(404, { message: `Student group with id ${params.id} not found` });
  }
}) satisfies PageLoad;
