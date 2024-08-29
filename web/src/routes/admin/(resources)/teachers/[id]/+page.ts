import { getSubjects, getTags, getTeacher, type Sort } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import { z } from 'zod';
import type { PageLoad } from './$types';

export const _schema = z.object({
  id: z.string(),
  firstName: z.string().trim().min(1, { message: 'Der Vorname darf nicht leer sein.' }),
  lastName: z.string().trim().min(1, { message: 'Der Nachname darf nicht leer sein.' }),
  acronym: z.string().trim().min(1, { message: 'Das Akroynm darf nicht leer sein.' }),
  tags: z.string().array(),
});

export const load = (async ({ params }) => {
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  const subjects = await getSubjects(sort);
  let formTeacher, title, teacherSubjects: string[];
  if (params.id === 'new') {
    formTeacher = { id: 'new', firstName: '', lastName: '', acronym: '', tags: [] };
    title = 'Lehrer — Hinzufügen';
    teacherSubjects = [];
  } else {
    try {
      const teacher = await getTeacher(params.id);
      title = `Teacher — ${teacher.firstName} ${teacher.lastName} (${teacher.acronym})`;
      teacherSubjects = teacher.subjects.map((subject) => subject.id);
      formTeacher = {
        ...teacher,
        tags: teacher.tags.map((tag) => tag.id),
      };
    } catch {
      error(404, { message: `Teacher with id ${params.id} not found` });
    }
  }
  return {
    form: await superValidate(formTeacher, zod(_schema), { errors: false }),
    tags,
    subjects,
    teacherSubjects,
    meta: {
      title,
    },
  };
}) satisfies PageLoad;
