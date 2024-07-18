import { getStudentGroup, type StudentGroup } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  if (params.id === 'new') {
    const studentGroup: StudentGroup = { id: 'new', name: '', grades: [], students: [], tags: [] };
    return {
      studentGroup,
      create: true,
      meta: {
        title: 'Schülergruppe — Hinzufügen',
      },
    };
  }
  try {
    const studentGroup = await getStudentGroup(params.id);
    return {
      studentGroup,
      create: false,
      meta: {
        title: `Schülergruppe — ${studentGroup.name}`,
      },
    };
  } catch {
    error(404, { message: `Student group with id ${params.id} not found` });
  }
}) satisfies PageLoad;
