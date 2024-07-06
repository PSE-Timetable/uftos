import { getStudent } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  try {
    const student = await getStudent(params.id);
    return {
      student,
      meta: {
        title: `Student — ${student.firstName} ${student.lastName}`,
      },
    };
  } catch {
    error(404, { message: `Student with id ${params.id} not found` });
  }
}) satisfies PageLoad;
