import { loadGrades, loadRoomPage, loadStudentPage, loadTeacherPage } from '$lib/utils/resources';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { getServerStats } from '$lib/sdk/fetch-client';

export const load = (async () => {
  try {
    return {
      initialGrades: await loadGrades('', ''),
      initialRooms: await loadRoomPage('', '', 0, 15),
      initialStudents: await loadStudentPage('', '', 0, 15),
      initialTeachers: await loadTeacherPage('', '', 0, 15),
      stats: await getServerStats(),
      meta: {
        title: 'Übersicht',
      },
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}) satisfies PageLoad;
