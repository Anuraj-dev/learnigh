# Learnigh — Product Vision

**Learnigh** is a Learnify/Humun-style personal learning tracker for Android. It helps Raja (and anyone like him) track *everything* they’re learning — paid courses, app subscriptions, YouTube playlists, freeCodeCamp, docs — in one offline-first place with deadlines and progress.

## Problem
Bought courses rot. Subscriptions renew unnoticed. YouTube playlists sprawl. There is no single mobile HQ that answers: *What am I learning? What’s due? Where do I open it?*

## Principles
1. **Mobile-first** — beautiful dark Material 3, thumb-friendly.
2. **Offline-first** — Room DB; no account required; no paywall.
3. **Source-agnostic** — Udemy, Coursera, Domestika, Notion, YouTube, freeCodeCamp, docs sites.
4. **Deadline-aware** — due-soon dashboard + calendar view.
5. **Streak-lite** — gentle habit signal without gamification spam.

## Course fields
| Field | Notes |
|-------|--------|
| title, provider | Required |
| source type | `website` \| `app_subscription` \| `youtube` \| `other_free` \| `paid_course` |
| URL / deep-link | Open in browser or app |
| purchase/start date | Optional |
| deadline | Powers due-soon + calendar |
| status | wishlist · not_started · in_progress · paused · completed · expired |
| progress % | 0–100 |
| notes, tags | Free text |
| reminder flag | Per-course |

## Screens
- **Home** — greeting, streak-lite, stats, due soon, in progress
- **Courses** — search + status/source filters
- **Add / Edit** — full form with date pickers
- **Detail** — open link, slider progress, status chips, delete
- **Deadlines** — month-grouped calendar list
- **Settings** — display name, reminder default, about

## Non-goals (v1)
- Cloud sync / multi-device
- In-app purchases or subscriptions
- Push notification scheduling (flag stored; OS alarms later)
- Social / sharing

## Success
Open Learnigh → see what’s due this week → tap Open link → update progress in under 10 seconds.
