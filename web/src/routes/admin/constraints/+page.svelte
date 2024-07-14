<script lang="ts">
  import { onMount } from 'svelte';
    import ConstraintSignatureComp from '$lib/components/ui/constraintSignature/constraint-signature.svelte';
    import {
        type ConstraintParameter,
        type ConstraintSignature,
        DefaultType, type Grade, type GradeResponseDto,
        ParameterType,
        getTeachers,
        type Subject,
        type Tag,
        type Teacher
    } from "$lib/sdk/fetch-client";

const constraintParameters: ConstraintParameter[] = [
        {
            id: '123',
            parameterName: 'Klasse X',
            parameterType: ParameterType.Grade
        },
        {
            id: '456',
            parameterName: 'Lehrer Y',
            parameterType: ParameterType.Teacher
        },
        {
            id: '789',
            parameterName: 'Fach Y',
            parameterType: ParameterType.Subject
        }
    ];

    const constraintSignature: ConstraintSignature = {
        parameters: constraintParameters,
        description: 'Klasse X hat Lehrer Y im Fach Z',
        name: 'test_constraint',
        defaultType: DefaultType.HardPenalize
    }

    const tag: Tag = {
        id: '123',
        name: 'qwerty'
    }

    const subjects: Subject[] = [
        {
            id: '123',
            name: 'Deutsch',
            tags: [ tag ]
        },
        {
            id: '456',
            name: 'English',
            tags: [ tag ]
        }
    ];

    const grades: GradeResponseDto[] = [
        {
            id: '123',
            name: '5.',
            studentIds: [],
            studentGroupIds: [],
            tags: []
        },
        {
            id: '456',
            name: '6.',
            studentIds: [],
            studentGroupIds: [],
            tags: []
        }
    ];

  let teachers: Teacher[] = [];

  onMount(async () => {
    try {
      const response = await getTeachers({ page: 0, size: 2 });
      teachers = response.content || [];
      teachers = teachers;
    } catch (error) {
      console.error('Error fetching teachers:', error);
    }
  });
</script>
<ConstraintSignatureComp {teachers} {grades} {subjects} {constraintSignature} />
