import { getTeacher } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  try {
    const teacher = await getTeacher(params.id);
    return {
      teacher,
      meta: {
        title: `Teacher — ${teacher.firstName} ${teacher.lastName} (${teacher.acronym})`,
      },
    };
  } catch {
    error(404, { message: `Teacher with id ${params.id} not found` });
  }
}) satisfies PageLoad;
