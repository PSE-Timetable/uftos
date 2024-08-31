import { getStudent, getTags, type Sort } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import { z } from 'zod';
import type { PageLoad } from './$types';

export const _schema = z.object({
  id: z.string(),
  firstName: z.string().trim().min(1, { message: 'Der Vorname darf nicht leer sein.' }),
  lastName: z.string().trim().min(1, { message: 'Der Nachname darf nicht leer sein.' }),
  tags: z.string().array(),
});

export const load = (async ({ params }) => {
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  let formStudent: { id: string; firstName: string; lastName: string; tags: string[] }, title: string;
  if (params.id === 'new') {
    formStudent = { id: 'new', firstName: '', lastName: '', tags: [] };
    title = 'Schüler — Hinzufügen';
  } else {
    try {
      const student = await getStudent(params.id);
      formStudent = {
        ...student,
        tags: student.tags.map((tag) => tag.id),
      };
      title = `Schüler — ${student.firstName} ${student.lastName}`;
    } catch {
      error(404, { message: `Student with id ${params.id} not found` });
    }
  }
  return {
    form: await superValidate(formStudent, zod(_schema), { errors: false }),
    tags,
    meta: {
      title,
    },
  };
}) satisfies PageLoad;
