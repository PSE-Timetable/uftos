import {
  deleteConstraintInstance,
  deleteGrade,
  deleteRoom,
  deleteStudent,
  deleteSubject,
  deleteTag,
  deleteTeacher,
  getConstraintInstances,
  getGrades,
  getRooms,
  getStudentGroup,
  getStudents,
  getSubjects,
  getTags,
  getTeachers,
  removeStudentsFromStudentGroup,
  type GradeResponseDto,
  type Pageable,
  type PageStudent,
  type PageTeacher,
  type Sort,
} from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import { toast as _toast } from 'svelte-sonner';

export type DataItem = {
  id: string;

  [key: string]: string | string[] | number;
};

export const toast = (success: boolean, message: string) =>
  success ? _toast.success(message) : _toast.error(message, { important: true, duration: 4000 });

export async function loadStudentPage(sortString: string, filter: string, index?: number, pageSize?: number) {
  const pageable: Pageable = { page: index, size: pageSize, sort: [sortString] };
  const result: PageStudent = await getStudents(pageable, {
    firstName: filter,
    lastName: filter,
  });
  const dataItems: DataItem[] = result.content
    ? result.content.map(
        (student): DataItem => ({
          id: student.id,
          firstName: student.firstName,
          lastName: student.lastName,
          tags: student.tags.map(({ name }) => name),
        }),
      )
    : [];
  return {
    data: dataItems,
    totalElements: Number(result.totalElements),
  };
}

export async function deleteStudentEntry(id: string) {
  try {
    await deleteStudent(id);
  } catch {
    toast(false, 'Beim Löschen des Schülers ist ein Fehler aufgetreten');
    error(400, { message: `could not delete student with id ${id}` });
  }
}

export async function loadRoomPage(sortString: string, filter: string, index?: number, pageSize?: number) {
  const pageable: Pageable = { page: index, size: pageSize, sort: [sortString] };
  const result = await getRooms(pageable, {
    name: filter,
    buildingName: filter,
  });
  const dataItems: DataItem[] = result.content
    ? result.content.map(
        (room): DataItem => ({
          id: room.id,
          name: room.name,
          buildingName: room.buildingName,
          capacity: room.capacity,
          tags: room.tags.map(({ name }) => name),
        }),
      )
    : [];
  return {
    data: dataItems,
    totalElements: Number(result.totalElements),
  };
}

export async function deleteRoomEntry(id: string) {
  try {
    await deleteRoom(id);
  } catch {
    toast(false, 'Beim Löschen des Raums ist ein Fehler aufgetreten');
    error(400, { message: `could not delete room with id ${id}` });
  }
}

export async function loadSubjects(sortString: string, filter: string) {
  const sort: Sort = { sort: [sortString] };
  const result = await getSubjects(sort, {
    name: filter,
  });
  const dataItems: DataItem[] = result.map(
    (subject): DataItem => ({
      id: subject.id,
      name: subject.name,
      tags: subject.tags.map((tag) => tag.name),
    }),
  );
  return {
    data: dataItems,
    totalElements: result.length,
  };
}

export async function deleteSubjectEntry(id: string) {
  try {
    await deleteSubject(id);
  } catch {
    toast(false, 'Beim Löschen des Fachs ist ein Fehler aufgetreten');
    error(400, { message: `could not delete subject with id ${id}` });
  }
}

export async function loadTags(sortString: string, filter: string) {
  const sort: Sort = { sort: [sortString] };

  const result = await getTags(sort, {
    name: filter,
  });
  const dataItems: DataItem[] = result.map((tag) => ({
    id: tag.id,
    name: tag.name,
  }));

  return {
    data: dataItems,
    totalElements: result.length,
  };
}

export async function deleteTagEntry(id: string) {
  try {
    await deleteTag(id);
  } catch {
    toast(false, 'Beim Löschen des Fachs ist ein Fehler aufgetreten');
    error(400, { message: `could not delete tag with id ${id}` });
  }
}

export async function loadTeacherPage(sortString: string, filter: string, index?: number, pageSize?: number) {
  const pageable: Pageable = { page: index, size: pageSize, sort: [sortString] };
  const result: PageTeacher = await getTeachers(pageable, {
    firstName: filter,
    lastName: filter,
    acronym: filter,
  });
  const dataItems: DataItem[] = result.content
    ? result.content.map(
        (teacher): DataItem => ({
          id: teacher.id,
          firstName: teacher.firstName,
          lastName: teacher.lastName,
          acronym: teacher.acronym,
          subjects: teacher.subjects.map((subject) => subject.name),
          tags: teacher.tags.map((tag) => tag.name),
        }),
      )
    : [];
  return {
    data: dataItems,
    totalElements: Number(result.totalElements),
  };
}

export async function deleteTeacherEntry(id: string) {
  try {
    await deleteTeacher(id);
  } catch {
    toast(false, 'Beim Löschen des Lehrers ist ein Fehler aufgetreten');
    error(400, { message: `could not delete teacher with id ${id}` });
  }
}

export async function loadGrades(sortString: string, filter: string) {
  const sort: Sort = { sort: [sortString] };
  const result: GradeResponseDto[] = await getGrades(sort, {
    name: filter,
  });
  const dataItems: DataItem[] = result.map((grade) => ({
    id: grade.id,
    name: grade.name,
    tags: grade.tags.map((tag) => tag.name),
  }));
  return {
    data: dataItems,
    totalElements: dataItems.length,
  };
}

export async function deleteGradeEntry(id: string) {
  try {
    await deleteGrade(id);
  } catch {
    toast(false, 'Beim Löschen der Stufe ist ein Fehler aufgetreten');
    error(400, { message: `could not delete grade with id ${id}` });
  }
}

export async function getInstancesPage(
  toSort: string,
  filter: string,
  pageIndex?: number,
  pageSize?: number,
  constraintSignatureId?: string,
) {
  const pageable: Pageable = { page: pageIndex, size: pageSize, sort: [toSort] };
  if (!constraintSignatureId) {
    throw error(400, { message: 'Could not fetch page' });
  }
  const result = await getConstraintInstances(constraintSignatureId, pageable, { argument: filter });
  const dataItems: DataItem[] = result.constraintInstances.map((instance) => {
    const item: DataItem = { id: instance.id };
    for (let i = 0; i < instance.arguments.length; i++) {
      item[`name${i}`] = result.displayNames.find((item) => item.id === instance.arguments[i].value)?.displayName ?? '';
    }
    return item;
  });
  return {
    data: dataItems,
    totalElements: dataItems.length,
  };
}

export async function deleteInstance(id: string, signatureId?: string) {
  try {
    await deleteConstraintInstance(signatureId || '', id);
  } catch {
    toast(false, 'Beim Löschen der Constraint Instanz ist ein Fehler aufgetreten');
    error(400, { message: `could not delete constraint instance with id ${id}` });
  }
}

export async function getStudentsFromGroup(
  toSort: string,
  filter: string,
  index?: number,
  pageSize?: number,
  additionalId?: string,
) {
  if (!additionalId) {
    throw error(400, { message: 'Invalid student group id' });
  }
  const studentGroup = await getStudentGroup(additionalId);
  const dataItems: DataItem[] = studentGroup.students.map(
    (student): DataItem => ({
      id: student.id,
      firstName: student.firstName,
      lastName: student.lastName,
      tags: student.tags.map(({ name }) => name),
    }),
  );
  return {
    data: dataItems,
    totalElements: Number(studentGroup.students.length),
  };
}

export async function removeStudentFromGroup(studentId: string, studentGroupId?: string) {
  if (!studentGroupId) {
    throw error(400, { message: `Student group id ${studentGroupId} is invalid` });
  }
  try {
    await removeStudentsFromStudentGroup(studentGroupId, [studentId]);
  } catch {
    toast(false, 'Beim Entfernen des Schülers aus einer Gruppe ist ein Fehler aufgetreten');
    error(400, { message: 'Could not remove student from group' });
  }
}
