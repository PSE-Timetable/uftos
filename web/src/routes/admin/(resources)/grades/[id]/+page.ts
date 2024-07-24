import { getGrade, getTags, type GradeResponseDto, type Sort } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  if (params.id === 'new') {
    const grade: GradeResponseDto = {
      id: 'new',
      curriculumId: '',
      name: '',
      studentGroupIds: [],
      studentIds: [],
      tags: [],
    };
    return {
      grade,
      tags,
      create: true,
      meta: {
        title: `Stufe — Hinzufügen`,
      },
    };
  }
  try {
    const grade = await getGrade(params.id);
    return {
      grade,
      tags,
      create: false,
      meta: {
        title: `Stufe — ${grade.name}`,
      },
    };
  } catch {
    error(404, { message: `Grade with id ${params.id} not found` });
  }
}) satisfies PageLoad;
