import type { Task, TaskCreateOrUpdate } from '../models/task';
import { API_BASE_URL } from '../utils/config';

const BASE_URL = `${API_BASE_URL}/api/tasks/`;

function extractMessage(data: unknown): string | null {
  if (typeof data === 'string') {
    return data;
  }

  if (!data || typeof data !== 'object') {
    return null;
  }

  const record = data as Record<string, unknown>;

  if (typeof record.detail === 'string') {
    return record.detail;
  }

  if (record.detail && typeof record.detail === 'object') {
    const nested = record.detail as Record<string, unknown>;
    for (const value of Object.values(nested)) {
      if (Array.isArray(value)) {
        return value.map(String).join(', ');
      }
      if (typeof value === 'string') {
        return value;
      }
    }
  }

  for (const value of Object.values(record)) {
    if (Array.isArray(value)) {
      return value.map(String).join(', ');
    }
    if (typeof value === 'string') {
      return value;
    }
  }

  return null;
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let message = 'Request failed';
    try {
      const data = await response.json();
      message = extractMessage(data) ?? message;
    } catch {
      // keep fallback message
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export const taskService = {
  getTasks(): Promise<Task[]> {
    return fetch(BASE_URL).then(handleResponse<Task[]>);
  },

  getTask(id: number): Promise<Task> {
    return fetch(`${BASE_URL}${id}/`).then(handleResponse<Task>);
  },

  createTask(task: TaskCreateOrUpdate): Promise<Task> {
    return fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(task),
    }).then(handleResponse<Task>);
  },

  updateTask(id: number, task: TaskCreateOrUpdate): Promise<Task> {
    return fetch(`${BASE_URL}${id}/`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(task),
    }).then(handleResponse<Task>);
  },

  deleteTask(id: number): Promise<void> {
    return fetch(`${BASE_URL}${id}/`, { method: 'DELETE' }).then(handleResponse<void>);
  },
};
