import { getGrade, getTags, type Sort } from '$lib/sdk/fetch-client';
import { init } from '$lib/utils/server';
import { error } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import { z } from 'zod';
import type { PageLoad } from './$types';

export const _schema = z.object({
  id: z.string(),
  name: z.string().trim().min(1, { message: 'Der Name darf nicht leer sein.' }),
  tags: z.string().array(),
  studentGroups: z.string().array(),
});

export const load = (async ({ params }) => {
  init();
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  let formGrade: { id: string; name: string; studentGroups: string[]; tags: string[] }, title: string;
  if (params.id === 'new') {
    formGrade = {
      id: 'new',
      name: '',
      studentGroups: [],
      tags: [],
    };
    title = `Stufe — Hinzufügen`;
  } else {
    try {
      const grade = await getGrade(params.id);
      formGrade = {
        ...grade,
        tags: grade.tags.map((tag) => tag.id),
        studentGroups: grade.studentGroupIds,
      };
      title = `Stufe — ${grade.name}`;
    } catch {
      error(404, { message: `Grade with id ${params.id} not found` });
    }
  }
  return {
    form: await superValidate(formGrade, zod(_schema), { errors: false }),
    tags,
    meta: {
      title,
    },
  };
}) satisfies PageLoad;
